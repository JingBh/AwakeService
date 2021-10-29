package top.jingbh.awakeservice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import top.jingbh.awakeservice.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
