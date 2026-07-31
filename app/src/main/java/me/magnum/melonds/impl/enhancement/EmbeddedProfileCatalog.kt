package me.magnum.melonds.impl.enhancement

import android.content.Context
import me.magnum.melonds.domain.model.enhancement.ProfileCatalog

class EmbeddedProfileCatalog(context: Context) {
    val catalog: ProfileCatalog by lazy {
        context.assets.open("enhancement-profiles.json").bufferedReader().use { ProfileCatalog.parse(it.readText()) }
    }
}
