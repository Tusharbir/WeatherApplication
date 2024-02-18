package tech.cseb.weatherapp.utility.loading_utils

import android.content.Context

open class LoadingUtils {
    companion object {
        private var customLoader: CustomLoader? = null
        fun showDialog(
            context: Context?,
            isCancelable: Boolean
        ) {
            hideDialog()
            if (context != null) {
                try {
                    customLoader = CustomLoader(context)
                    customLoader?.let { customLoader->
                        customLoader.setCanceledOnTouchOutside(true)
                        customLoader.setCancelable(isCancelable)
                        customLoader.show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        fun hideDialog() {
            if (customLoader !=null && customLoader?.isShowing!!) {
                customLoader = try {
                    customLoader?.dismiss()
                    null
                } catch (e: Exception) {
                    null
                }
            }
        }

    }
}