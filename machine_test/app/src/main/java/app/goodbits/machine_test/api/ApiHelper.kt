package app.goodbits.machine_test.api


import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.xbill.DNS.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response
import java.io.IOException
import java.net.InetAddress

import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.IntRange
import com.google.gson.JsonObject
import com.tailormade.api.*
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import kotlin.jvm.Throws

object ApiHelper {

    val base_url = "https://api.covid19india.org/"

    var sessionId: String = ""

    lateinit var apiService: ApiService

    //For DNS
    var mInitialized = false
    private const val LIVE_API_HOST = "easytaxi.com.br"
    private const val LIVE_API_IP = "8.8.8.8"
    private lateinit var mLiveApiStaticIpAddress: InetAddress

    private val okHttpClient = enableTls120nPreLollipop(
        OkHttpClient().newBuilder()
            .apply {
                readTimeout(20, TimeUnit.SECONDS)
                connectTimeout(20, TimeUnit.SECONDS)
                writeTimeout(20, TimeUnit.SECONDS)
                interceptors().addAll(getInterceptors())
            }).build()

    init {
        makeService()
    }

    private fun makeService() {
        val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    private fun getInterceptors(): List<Interceptor> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val authInterceptor: Interceptor = object : Interceptor {

            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val original: Request = chain.request()
                val builder: Request.Builder = original.newBuilder()
                    .header("Authorization", Credentials.basic("api_admin", "admin123"))
                    /*.header(
                        "Authorization", "Bearer${
                            SharedPreferenceHelper().getString(
                                SharedPreferenceHelper.SESSION_TOKEN,
                                ""
                            )
                        }"
                    )*/

                val request: Request = builder.build()
                return chain.proceed(request)
            }

        }
        return mutableListOf<Interceptor>().apply {
            add(loggingInterceptor)
            add(authInterceptor)
        }.toList()
    }

    suspend fun <T> safeApiCall(
        handleError: Boolean = true,
        handleLoading: Boolean = true,
        context: Context,
        call: suspend () -> Response<T>
    ): NetWorkCall<T> {
        var dialog: Dialog? = null
        if (handleLoading)
            Handler(context.mainLooper).post(Runnable {
                if (handleLoading)
                    dialog = ApiUiHelper.defaultLoadingDialog(context)
            })
       return try {
           val myResp = call.invoke()
           Handler(context.mainLooper).postDelayed(Runnable {
               dialog?.dismiss()
           },400)
           if (myResp.isSuccessful){
               when {
                   myResp.body() is JsonObject -> {
                       NetWorkCall.Success(myResp.body()!!)
                   }
                   myResp.body() is String -> {
                       NetWorkCall.Success(myResp.body()!!)
                   }
                   else -> {
                       NetWorkCall.Error(myResp.body().toString())
                   }
               }
           }else{
               Handler(context.mainLooper).postDelayed(Runnable {
                   dialog?.dismiss()
               },400)

               var errorMessage : String = myResp.message()
               if (myResp.code() == 400){
                   val jObjError = JSONObject(myResp.errorBody()?.string())
                   Log.e("TAG", "safeApiCall:$jObjError")
                   errorMessage = jObjError.getString("message").toString()
               }

               if (handleError){
                   if (myResp.code() != HttpsURLConnection.HTTP_FORBIDDEN){
                       if (myResp.code() == 400){
                           Handler(Looper.getMainLooper()).post ( Runnable {
                               Toast.makeText(context, "$errorMessage", Toast.LENGTH_SHORT).show()
                           } )
                       }else{
                           Handler(Looper.getMainLooper()).post(Runnable {
                               Toast.makeText(
                                   context,
                                   "${myResp.code()} : $errorMessage : ${
                                       myResp.errorBody()?.toString()
                                   }",
                                   Toast.LENGTH_LONG
                               ).show()
                           })
                       }
                   }
               }
               NetWorkCall.Error(errorMessage)

           }
       }catch (e:Exception){
           Log.e("ApiHelper", "Exception", e)
           Handler(Looper.getMainLooper()).postDelayed(Runnable {
               dialog?.cancel()
           },400)
           if (getConnectionType(context) == 0){
               Handler(context.mainLooper).postDelayed(Runnable {
                   dialog?.cancel()
                   ApiUiHelper.ShowApiRetryAlert(
                       "Oops! No internet connection",
                       "Please check your internet connection and try again",
                       context
                   ) {
                       Log.e("ERR", "DFS")
                       safeApiCall(handleError, handleLoading, context, call)
                   }
               },400)
               NetWorkCall.Error("")
           }else{
               Handler(Looper.getMainLooper()).post(Runnable {
                   if(e?.localizedMessage.contains("Unable to resolve host ") ||
                       e.localizedMessage.contains("No address associated with hostname") ||
                       e?.localizedMessage.contains("StandaloneCoroutine was cancelled")
                   ){
                       Handler(context.mainLooper).postDelayed(Runnable {
                           dialog?.cancel()
                           ApiUiHelper.ShowApiRetryAlert(
                               "Oops! No internet connection",
                               "Please check your internet connection and try again",
                               context
                           ) {
                               Log.e("ERR", "DFS")
                               safeApiCall(handleError, handleLoading, context, call)
                           }
                       },400)
                       NetWorkCall.Error("")
                   }else{
                       Handler(Looper.getMainLooper()).post(Runnable {
                         if (!e?.localizedMessage.contains("JsonReader.setLenient(true)")&& !e?.localizedMessage.contains("routine")){
                             Toast.makeText(
                                 context,
                                 "An error occurred : ${e?.localizedMessage}",
                                 Toast.LENGTH_LONG
                             ).show()
                         }
                       })
                       NetWorkCall.Error("An error occurred : ${e?.localizedMessage}")
                   }
               })
               NetWorkCall.Error("An error occurred : ${e?.localizedMessage}")
           }
       }

    }


    @IntRange(from = 0, to = 2)
    fun getConnectionType(context: Context):Int{
        var result = 0
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if (capabilities != null){
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    result = 2
                }else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                    result = 1
                }
            }
        }else{
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null){
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI){
                    result = 2
                }else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE){
                    result = 1
                }
            }
        }
        return result
    }


    private fun init(){
        if (mInitialized)return else mInitialized = true

        try {
            mLiveApiStaticIpAddress = InetAddress.getByName(LIVE_API_IP)
        }catch (e:UnknownHostException){
            Log.w("“DNS”", "Couldn't initialize static IP address")
        }

        try {
            val defaultResolver = Lookup.getDefaultResolver()
            val googleFirstResolver : Resolver = SimpleResolver("8.8.8.8")
            val googleSoundResolver:Resolver = SimpleResolver("8.8.4.4")
            val amazoneResolver:Resolver = SimpleResolver("205.251.198.30")
            Lookup.setDefaultResolver(
                ExtendedResolver(
                    arrayOf(
                        defaultResolver,googleFirstResolver,googleSoundResolver,amazoneResolver
                    )
                )
            )
        }catch (e:UnknownHostException){
            Log.w("“DNS”", "Couldn't initialize custom resolvers")
        }

    }


    fun enableTls120nPreLollipop(client: OkHttpClient.Builder):OkHttpClient.Builder{
        if (Build.VERSION.SDK_INT < 22){
            try {
                val tlsTocketFactory = TLS12SocketFactory()
                val sc = SSLContext.getInstance("TLSv1.2")
                sc.init(null,null,null)
                tlsTocketFactory.trustManager?.let {
                    client.sslSocketFactory(tlsTocketFactory,it)
                }
                val cs:ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .build()
                val specs:MutableList<ConnectionSpec> = ArrayList()
                specs.add(cs)
                specs.add(ConnectionSpec.COMPATIBLE_TLS)
                specs.add(ConnectionSpec.CLEARTEXT)
                client.connectionSpecs(specs)
            }catch (exc:java.lang.Exception){
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc)
            }
        }
        return client
    }

}