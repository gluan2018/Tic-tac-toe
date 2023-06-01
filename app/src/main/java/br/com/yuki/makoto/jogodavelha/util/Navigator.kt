package br.com.yuki.makoto.jogodavelha.util

import android.view.ViewGroup
import androidx.annotation.AnimRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import br.com.yuki.makoto.jogodavelha.R

@Suppress("MemberVisibilityCanBePrivate")
class Navigator private constructor(
    private val container: FragmentContainerView,
    private val fragmentManager: FragmentManager,
) {

    private data class Animation(
        @AnimRes val enter: Int,
        @AnimRes val exit: Int,
        @AnimRes val popEnter: Int,
        @AnimRes val popExit: Int
    )

    companion object {
        @JvmStatic
        fun of(host: FragmentContainerView): Navigator {
            return Navigator(
                container = host,
                fragmentManager = host.context.cast<AppCompatActivity>().supportFragmentManager
            )
        }

        @JvmStatic
        fun of(host: FragmentContainerView, fragmentManager: FragmentManager): Navigator {
            return Navigator(
                container = host,
                fragmentManager = fragmentManager
            )
        }

        @JvmStatic
        fun Fragment.findNavigation(): Navigator? {
            val rootView = activity?.findViewById<ViewGroup>(android.R.id.content)?.cast<ViewGroup>() ?: return null
            return findViewGroup(rootView)?.run(Navigator::of)
        }

        @JvmStatic
        private fun findViewGroup(group: ViewGroup): FragmentContainerView? {
            for (index in 0 until  group.childCount) {
                when (val view = group[index]) {
                    is FragmentContainerView -> return view
                    is ViewGroup -> return findViewGroup(view)
                }
            }

            return null
        }
    }

    private val animation: Animation = Animation(
        enter = R.anim.full_enter_fragment,
        exit = R.anim.full_exit_fragment,
        popEnter = R.anim.full_pop_enter_fragment,
        popExit = R.anim.full_pop_exit_fragment
    )

    fun present(fragment: Fragment) = show(fragment = fragment, addToBack = false)

    fun show(
        fragment: Fragment,
        addToBack: Boolean = true,
    ) {
        fragmentManager.commit {
            setCustomAnimations(animation.enter, animation.exit, animation.popEnter, animation.popExit)
            replace(container.id, fragment, fragment::class.java.name)
            if (addToBack)
                addToBackStack(fragment::class.java.name)
        }
    }

    fun show(
        tagShow: String,
        tagHide: String? = null
    ) {
        fragmentManager.commit {
            setCustomAnimations(animation.enter, animation.exit, animation.popEnter, animation.popExit)
            if (tagHide != null)
                fragmentManager.findFragmentByTag(tagHide)?.run(this::hide)
            fragmentManager.findFragmentByTag(tagShow)?.run(this::show)
        }
    }

    fun add(list: List<Fragment>) {
        if (list.isEmpty())
            throw IllegalArgumentException("List of fragment cannot be empty")
        fragmentManager.commit {
            setCustomAnimations(animation.enter, animation.exit, animation.popEnter, animation.popExit)
            list.forEach { fragment ->
                add(container.id, fragment, fragment::class.java.name)
                hide(fragment)
            }
            show(list.first())
        }
    }

    fun dismiss() {
        fragmentManager.popBackStack()
    }

}
@Suppress("unchecked_cast")
private fun <T> Any.cast() = this as T