package app.goodbits.machine_test.api

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import app.goodbits.machine_test.R
import java.lang.Exception

object ApiUiHelper {

    fun ShowApiRetryAlert(header:String,
                          message: String,
                          context: Context,
                          retryClickListener:suspend () ->Unit
    ) {
        try {
            val builder:AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(header)
            builder.setMessage(message)
            builder.setCancelable(false)
            builder.setPositiveButton(
                "Retry",fun(dialog:DialogInterface,which:Int){
                    Log.e("BEFORE", "FD")
                    Log.e("AFTER", "FD")
                    Log.e("OUTSIDE", "FD")
                    dialog.dismiss()
                }
            )
            val dialog = builder.show()
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(context.resources.getColor(android.R.color.holo_orange_dark))
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(context.resources.getColor(android.R.color.holo_orange_dark))
        }catch (e:Exception){
            Log.e("API","ShopApiRetryAlert: ",e)
        }

    }

    fun defaultLoadingDialog(context: Context): Dialog? {
        var dialog:Dialog? = Dialog(context, R.style.LoadingDialogWithStatusBarColor)
        try {
            dialog?.setCancelable(true)
            val mInflater:LayoutInflater = LayoutInflater.from(context)
            val mView:View = mInflater.inflate(R.layout.loading_dialog,null)
            dialog?.setContentView(mView)
            dialog?.show()
        }catch (e:java.lang.Exception){
            Log.e("API", "defaultLoadingDialog: ", e)
            dialog = null
        }
        return dialog
    }

}