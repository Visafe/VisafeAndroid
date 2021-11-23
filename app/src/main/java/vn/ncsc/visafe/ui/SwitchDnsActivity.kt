package vn.ncsc.visafe.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_switch_dns.*
import vn.ncsc.visafe.R

class SwitchDnsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_switch_dns)

        radio_group.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)

                Toast.makeText(applicationContext," On checked change : ${radio.text}", Toast.LENGTH_SHORT).show()
            // set DNS
                when (checkedId) {
                    R.id.radio0 ->{

                    }
                    R.id.radio1 ->{

                    }
                    R.id.radio2 ->{

                    }
                    R.id.radio3 ->{

                    }
                    else ->{}

                }

            })

        iv_back.setOnClickListener {
            finish()
        }

    }
}