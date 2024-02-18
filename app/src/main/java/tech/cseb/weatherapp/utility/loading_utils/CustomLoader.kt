package tech.cseb.weatherapp.utility.loading_utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import tech.cseb.weatherapp.R

class CustomLoader(context: Context): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_loader)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setGravity(Gravity.CENTER)
        window?.setBackgroundDrawableResource(R.color.transparent_background)
    }
}