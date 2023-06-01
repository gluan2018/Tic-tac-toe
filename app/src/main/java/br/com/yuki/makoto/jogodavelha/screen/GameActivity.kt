package br.com.yuki.makoto.jogodavelha.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.yuki.makoto.jogodavelha.databinding.GameActivityBinding
import br.com.yuki.makoto.jogodavelha.screen.fragment.MenuGameFragment
import br.com.yuki.makoto.jogodavelha.util.Navigator

class GameActivity : AppCompatActivity() {
    private val binding: GameActivityBinding by lazy {
        GameActivityBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Navigator.of(binding.container)
            .present(MenuGameFragment())
    }
}