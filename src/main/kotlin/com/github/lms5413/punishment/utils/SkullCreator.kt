package com.github.lms5413.punishment.utils

import com.destroystokyo.paper.profile.ProfileProperty
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

fun createHead(base64: String): ItemStack {
    val skull = ItemStack(Material.PLAYER_HEAD)
    val skullMeta = skull.itemMeta as SkullMeta
    skullMeta.playerProfile = Bukkit.createProfile(UUID.randomUUID(), null)
    val playerProfile = skullMeta.playerProfile
    playerProfile?.properties?.add(ProfileProperty("textures", base64))
    skullMeta.playerProfile = playerProfile
    skull.itemMeta = skullMeta

    return skull
}

fun createHead(player: OfflinePlayer, nickname: String): ItemStack {
    val skull = ItemStack(Material.PLAYER_HEAD)
    val skullMeta = skull.itemMeta as SkullMeta

    val name = player.name ?: nickname

    if (name.length <= 16) {
        val profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(name.toByteArray()), name)
        skullMeta.playerProfile = profile
    }

    skull.itemMeta = skullMeta
    return skull
}
