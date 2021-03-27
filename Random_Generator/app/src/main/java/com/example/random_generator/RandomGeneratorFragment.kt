package com.example.random_generator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_random_generator.*
import kotlinx.android.synthetic.main.fragment_random_generator.view.*
import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.math.BigDecimal
import java.util.*
import kotlin.math.pow

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val RESULT = "result"
private val random = Random()

/**
 * A simple [Fragment] subclass.
 * Use the [RandomGeneratorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RandomGeneratorFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var result: BigDecimal = BigDecimal(0)
    private val listRandomValue = mutableListOf<BigDecimal>()
    private var quantity = 0
    private var decimalPlaces = 0
    private var resultString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_random_generator, container, false)

        view.random_button.setOnClickListener(this::onRandomButtonClick)
        view.copy_button.setOnClickListener(this::onCopyButtonClick)

        view.quantity_label.text = String.format(Locale.US, "%s %d",
            getString(R.string.quantity_label), view.quantity_seek_bar.progress + 1)

        view.quantity_seek_bar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                quantity = p1
                view.quantity_label.text = String.format(Locale.US, "%s %d",
                    getString(R.string.quantity_label), quantity + 1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        view.decimal_places_label.text = String.format(Locale.US, "%s %d",
            getString(R.string.decimal_places_label), view.decimal_places_seek_bar.progress)

        view.decimal_places_seek_bar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                decimalPlaces = p1
                view.decimal_places_label.text = String.format(Locale.US, "%s %d",
                    getString(R.string.decimal_places_label), decimalPlaces)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })


        view.result_label.text = resultString

        return view
    }

    private fun stringBuilderFormation(list: List<BigDecimal>): StringBuilder {
        val stringBuilder = StringBuilder()
        for (result in list) {
            stringBuilder.append(String.format(Locale.US, "%."+ decimalPlaces + "f, ", result))
        }
        if (stringBuilder.isNotEmpty()) {
            stringBuilder.delete(stringBuilder.length - 2, stringBuilder.length - 1)
        }
        return stringBuilder
    }

    private fun onRandomButtonClick(view: View) {
        if (min_value_text.text.isNotEmpty() && max_value_text.text.isNotEmpty()) {
            var min: Long = 0
            var max: Long = 0

            try {
                min = min_value_text.text.toString().toLong()
                max = max_value_text.text.toString().toLong()
            } catch (ex: NumberFormatException) {
                Toast.makeText(activity, R.string.error_parse, Toast.LENGTH_SHORT).show()
                return
            }

            resultString = ""
            if (min < max) {
                var i = 0
                while (i < quantity + 1) {
                    result = BigDecimal(random.nextDouble() * (max - min) + min)
                    result = result.setScale(decimalPlaces, BigDecimal.ROUND_DOWN)

                    if (not_repeating_check_box.isChecked) {
                        if (decimalPlaces == 0 && max - min < quantity || decimalPlaces > 0
                            && quantity * decimalPlaces > (max - min) * 10.0.pow(
                                decimalPlaces.toDouble())) {
                            Toast.makeText(activity, R.string.error_quantity_generate, Toast.LENGTH_SHORT).show()
                            break
                        }
                        if (listRandomValue.isNotEmpty() && listRandomValue.contains(result)) {
                            continue
                        }
                    }
                    listRandomValue.add(result)
                    ++i
                }
                resultString = stringBuilderFormation(listRandomValue).toString()
                result_label.text = resultString
                listRandomValue.clear()
            } else Toast.makeText(activity, R.string.error_generate, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onCopyButtonClick(view: View) {
        if (result_label.text.isNotEmpty()) {
            val clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(RESULT, result_label.text.toString())
            clipboard.setPrimaryClip(clip)

            Toast.makeText(activity, R.string.copy_toast, Toast.LENGTH_SHORT).show()
        } else
            Toast.makeText(activity, R.string.copy_error, Toast.LENGTH_SHORT).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RandomGeneratorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            RandomGeneratorFragment().apply {
                arguments = Bundle().apply {
                    putString(RESULT, param1)
                }
            }
    }
}