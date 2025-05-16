package com.github.lms5413.punishment.gui

import com.github.lms5413.punishment.Punishment
import com.github.lms5413.punishment.utils.convertTimeToString
import com.github.lms5413.punishment.utils.createHead
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit

class PunishmentHistoryMainGui: PagedInventory("punishment.history", "Punishment History", 3 * 9) {


    override fun configureViewer(viewer: PagedViewer) {
        val configuration = viewer.configuration
        configuration.itemPageLimit(7)
    }

    override fun createPageItems(p0: PagedViewer): List<InventoryItemSupplier?>? {
        val suppliers = mutableListOf<InventoryItemSupplier?>()
        val history = Punishment.getInstance().getPunishmentManager().getPunishments().sortedByDescending { it.id }

        for (punishment in history) {

            val player = Bukkit.getOfflinePlayer(punishment.uuid)
            val title = Punishment.getInstance().config.getComponentString("gui-history.main.title")
                .replaceText {
                    it.matchLiteral("{player}").replacement(punishment.name)
                }
                .color(TextColor.fromHexString("#FF0000"))
                .style(Style.empty().decoration(TextDecoration.ITALIC, false))

            val lore = Punishment.getInstance().config.getComponentStringList("gui-history.main.lore")
                .map {
                    it.replaceText { text ->
                        text.matchLiteral("{player}").replacement(punishment.name)
                    }.replaceText { text ->
                        text.matchLiteral("{reason}").replacement(punishment.reason ?: "No reason provided")
                    }.replaceText { text ->
                        text.matchLiteral("{author}").replacement(punishment.author)
                    }.replaceText { text ->
                        text.matchLiteral("{time}").replacement(convertTimeToString((punishment.timeout ?: System.currentTimeMillis()) - System.currentTimeMillis()))
                    }.replaceText { text ->
                        text.matchLiteral("{type}").replacement(punishment.type.toString())
                    }.replaceText { text ->
                        text.matchLiteral("{ip}").replacement(punishment.ip ?: "No IP provided")
                    }.replaceText { text ->
                        text.matchLiteral("{id}").replacement(punishment.id.toString())
                    }.replaceText { text ->
                        text.matchLiteral("{active}").replacement(punishment.isActive.toString())
                    }.style(Style.empty().decoration(TextDecoration.ITALIC, false))
                }

            suppliers.add {
                val item = createHead(player, punishment.name)
                val meta = item.itemMeta

                meta.displayName(title)
                meta.lore(lore)

                item.itemMeta = meta
                InventoryItem.of(item)

            }
        }
        return suppliers
    }
}