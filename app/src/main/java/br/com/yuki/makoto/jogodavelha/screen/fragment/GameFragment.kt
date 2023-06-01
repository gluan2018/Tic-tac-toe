package br.com.yuki.makoto.jogodavelha.screen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.yuki.makoto.jogodavelha.databinding.GameFragmentBinding

class GameFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        GameFragmentBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GameFragmentBinding.bind(view).apply {
            game.setOnGameFinishedListener { winner ->
                WinFragment(winner) {
                    game.reset()
                }.show(childFragmentManager, null)
            }
        }
    }
}