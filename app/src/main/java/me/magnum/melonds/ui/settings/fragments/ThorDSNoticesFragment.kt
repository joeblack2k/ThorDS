package me.magnum.melonds.ui.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import me.magnum.melonds.R
import me.magnum.melonds.ui.settings.PreferenceFragmentTitleProvider
import me.magnum.melonds.ui.theme.MelonTheme

class ThorDSNoticesFragment : Fragment(), PreferenceFragmentTitleProvider {
    override fun getTitle() = getString(R.string.thords_about_notices)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MelonTheme {
                    val notices = remember {
                        resources.openRawResource(R.raw.third_party_notices).bufferedReader().use { it.readText() }
                    }
                    Text(
                        text = notices,
                        modifier = Modifier.fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .safeDrawingPadding()
                            .padding(16.dp),
                        style = MaterialTheme.typography.body1,
                    )
                }
            }
        }
    }
}
