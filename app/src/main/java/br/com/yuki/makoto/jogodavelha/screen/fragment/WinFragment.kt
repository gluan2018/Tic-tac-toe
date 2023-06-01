package br.com.yuki.makoto.jogodavelha.screen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import br.com.yuki.makoto.jogodavelha.R
import br.com.yuki.makoto.jogodavelha.databinding.WinnerFragmentBinding
import br.com.yuki.makoto.jogodavelha.view.point.CirclePoint
import br.com.yuki.makoto.jogodavelha.view.point.SignedPoint

class WinFragment(
    private val winner: SignedPoint?,
    private val restart: () -> Unit
) : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.MaterialAlertDialog_Material3)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        WinnerFragmentBinding.inflate(inflater, container, false).root
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        WinnerFragmentBinding.bind(view).apply {
            when (winner) {
                null -> {
                    iconWinner.setImageResource(R.drawable.draw)
                    titleWinner.text = "Empate"
                }
                else -> iconWinner.setImageResource(if (winner is CirclePoint) R.drawable.circle else R.drawable.x)
            }
            buttonRestart.setOnClickListener {
                restart()
                this@WinFragment.dismiss()
            }
        }
    }
}