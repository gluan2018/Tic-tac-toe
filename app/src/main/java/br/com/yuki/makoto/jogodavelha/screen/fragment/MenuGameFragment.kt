package br.com.yuki.makoto.jogodavelha.screen.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.yuki.makoto.jogodavelha.databinding.MenuGameFragmentBinding
import br.com.yuki.makoto.jogodavelha.util.GameController
import br.com.yuki.makoto.jogodavelha.util.Navigator.Companion.findNavigation
import br.com.yuki.makoto.jogodavelha.view.GameView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MenuGameFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        MenuGameFragmentBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MenuGameFragmentBinding.bind(view).apply {
            buttonStart.setOnClickListener {
                findNavigation()?.present(GameFragment())
            }
            lifecycleScope.launch {
                while (true) {
                    val controllers = GameController.getGamesControllers()
                    val colors = GameView.colors()

                    controllers.firstOrNull()?.let {
                        controller1.imageTintList = ColorStateList.valueOf(colors.component1())
                    }

                    controllers.getOrNull(1)?.let {
                        controller2.imageTintList = ColorStateList.valueOf(colors.component2())
                    }

                    if (controllers.size >= 2)
                        break

                    delay(1000)
                }
            }
        }
    }
}