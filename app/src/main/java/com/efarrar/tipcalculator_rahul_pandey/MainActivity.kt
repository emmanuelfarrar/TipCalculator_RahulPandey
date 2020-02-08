package com.efarrar.tipcalculator_rahul_pandey

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

/*
    EP 1: Outline of the project
    EP 2: Initial setup of textView, labels, seekBar
    EP 3: Adding functional code
          Step 1: Set max to seekBar 30
          Step 2: Add code to show change in movement on seekBar to alter tvTipPercent
          Step 3: Add code to catch changes in etBase and update textViews
          Step 4: Call computeTipAndTotal() created in step 3 so displayed amounts change as slider moves
          Step 5: Fix bugs and formatting for proper currency amounts
            5.1: Bug crashes app when clearing the values in etBase
            5.2: formatting the decimal point so only 2 after point
    EP 4: Adding design elements to the app. Color change, creative text that changes based on percentage number, footer
            6.1 Added tvFooter, emoji, all caps, other formatting to this tv
            6.2 Color scheme for the file added colorBackground (colors.xml/styles.xml)
            6.3 Added tvTipDescription and code for it to change when we us onProgressChanged()
            6.4 Changing the color of tvTipDescription using Interpolation via ArgbEvaluator class
 */

/*
    Notes about the code:
    URL: https://youtu.be/29qX_-ckZkQ
    1) The functionality is that of as soon as we input a value in the Base the
    calculation of the base value and tip percentage takes place.
    2)
 */

//tag to be notified when the method is being called.
private const val TAG = "MainActivity" //(Step 1)
private const val INITIAL_TIP_PERCENT = 15  //const for initial tip percent to be used w/ seekBar and tvTipPercent (Step 2.3b)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Setting initial seekBar value and tvTipPercent to 15 (Step 2.3)
        seekBarTip.progress = INITIAL_TIP_PERCENT //15
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%" //15
        updateTipDescription(INITIAL_TIP_PERCENT)   //(Step 6.1c)

        //Listener that is used to return data when the seekBar is changed (Step 2.1)
        seekBarTip.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")       //log entry to show when progress is called

                //when the progress is changed update the tvTipPercentage (Step 2.2)
                tvTipPercent.text = "$progress%"

                //calling updateTipDescription() to update the tvTipDescription (Step 6.1)
                updateTipDescription(progress)

                computeTipAndTotal()        //(Step 4.1)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        //listener for etBase that listens for changes in text. (Step 3.1)
        etBase.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s") //s gets us the value that the user is typing
                computeTipAndTotal()        //(Step 3.2)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }

    //function to update tvTipDescription as seekBar changes 6.1b
    private fun updateTipDescription(tipPercent: Int) {
       val tipDescription : String
        when (tipPercent) {
            in 0..9 -> tipDescription = "Poor"
            in 10..14 -> tipDescription = "Acceptable"
            in 15..19 -> tipDescription = "Good"
            in 20..24 -> tipDescription = "Great"
            else -> tipDescription = "Amazing"
        }
        //once we have the value update the tvTipDescription
        tvTipDescription.text = tipDescription

        /* (Step 6.4)
        Using ArgbEvaluator class we can get the value from tipPercent cast it to a floating value since ArgbEvaluator
        works with float values and then set the color scale to go between colorWorstTip -> colorBestTip
        (red ->green). Then cast it as int.
        - After this set it to a value/variable (val color) and then set the tvTipDescription to color val
        Note the setTextColor method uses Int so this is why we have to cast back to Int
         */
        val color = ArgbEvaluator().evaluate(tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.colorWorstTip),
            ContextCompat.getColor(this, R.color.colorBestTip)
        ) as Int

        tvTipDescription.setTextColor(color)
    }

    //(Step 3.2)
    private fun computeTipAndTotal() {
        // Get the value of the base and tip percent

        /*
            //Step 5.1
            create a if statement to set the text for tvTipAmount and tvTotalAmount to empty
            and early return from the the statement to avoid th crash bug that happens.
         */
        if (etBase.text.toString().isEmpty()){
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        } //Step 5.1

        val baseAmount = etBase.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount

        /*Assigning tipAmount and totalAmount to ids to be displayed(Step 3.3)
           Step 5.2 changed to reflect the numbers of decimals needed
         */
        tvTipAmount.text = "%.2f".format(tipAmount)              //tvTipAmount.text = tipAmount.toString()
        tvTotalAmount.text = "%.2f".format(totalAmount)         //tvTotalAmount.text = totalAmount.toString()
    }
}
