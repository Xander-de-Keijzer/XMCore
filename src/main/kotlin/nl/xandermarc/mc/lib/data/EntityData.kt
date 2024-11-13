package nl.xandermarc.mc.lib.data

import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Pose
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.*
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmName

@Suppress("UNCHECKED_CAST", "UNUSED")
data object EntityData {
    private fun KClass<*>.member(name: String): KCallable<*> =
        members.firstOrNull { it.name == name } ?: throw NoSuchFieldException("$jvmName.$name")

    private fun <T> accessor(kClass: KClass<*>, name: String) =
        kClass.member(name).apply { isAccessible = true }.call() as EntityDataAccessor<T>

    // Entity
    val SHARED_FLAGS_ID = accessor<Byte>(Entity::class, "DATA_SHARED_FLAGS_ID")
    val AIR_SUPPLY_ID = accessor<Short>(Entity::class, "DATA_AIR_SUPPLY_ID")
    val CUSTOM_NAME = accessor<Optional<Component>>(Entity::class, "DATA_CUSTOM_NAME")
    val CUSTOM_NAME_VISIBLE = accessor<Boolean>(Entity::class, "DATA_CUSTOM_NAME_VISIBLE")
    val SILENT = accessor<Boolean>(Entity::class, "DATA_SILENT")
    val NO_GRAVITY = accessor<Boolean>(Entity::class, "DATA_NO_GRAVITY")
    val POSE = accessor<Pose>(Entity::class, "DATA_POSE")
    val TICKS_FROZEN = accessor<Int>(Entity::class, "DATA_TICKS_FROZEN")

    // Display
    val TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID =
        accessor<Int>(Display::class, "DATA_TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID")
    val TRANSFORMATION_INTERPOLATION_DURATION_ID =
        accessor<Int>(Display::class, "DATA_TRANSFORMATION_INTERPOLATION_DURATION_ID")
    val POS_ROT_INTERPOLATION_DURATION_ID = accessor<Int>(Display::class, "DATA_POS_ROT_INTERPOLATION_DURATION_ID")
    val TRANSLATION_ID = accessor<Vector3f>(Display::class, "DATA_TRANSLATION_ID")
    val SCALE_ID = accessor<Vector3f>(Display::class, "DATA_SCALE_ID")
    val LEFT_ROTATION_ID = accessor<Quaternionf>(Display::class, "DATA_LEFT_ROTATION_ID")
    val RIGHT_ROTATION_ID = accessor<Quaternionf>(Display::class, "DATA_RIGHT_ROTATION_ID")
    val BILLBOARD_RENDER_CONSTRAINTS_ID = accessor<Byte>(Display::class, "DATA_BILLBOARD_RENDER_CONSTRAINTS_ID")
    val BRIGHTNESS_OVERRIDE_ID = accessor<Int>(Display::class, "DATA_BRIGHTNESS_OVERRIDE_ID")
    val VIEW_RANGE_ID = accessor<Float>(Display::class, "DATA_VIEW_RANGE_ID")
    val SHADOW_RADIUS_ID = accessor<Float>(Display::class, "DATA_SHADOW_RADIUS_ID")
    val SHADOW_STRENGTH_ID = accessor<Float>(Display::class, "DATA_SHADOW_STRENGTH_ID")
    val WIDTH_ID = accessor<Float>(Display::class, "DATA_WIDTH_ID")
    val HEIGHT_ID = accessor<Float>(Display::class, "DATA_HEIGHT_ID")
    val GLOW_COLOR_OVERRIDE_ID = accessor<Int>(Display::class, "DATA_GLOW_COLOR_OVERRIDE_ID")

    // Display.BlockDisplay
    val BLOCK_STATE_ID = accessor<BlockState>(Display.BlockDisplay::class, "DATA_BLOCK_STATE_ID")

    // Display.ItemDisplay
    val ITEM_STACK_ID = accessor<ItemStack>(Display.ItemDisplay::class, "DATA_ITEM_STACK_ID")
    val ITEM_DISPLAY_ID = accessor<Byte>(Display.ItemDisplay::class, "DATA_ITEM_DISPLAY_ID")

    // Display.TextDisplay
    val TEXT_ID = accessor<Component>(Display.TextDisplay::class, "DATA_TEXT_ID")
    val LINE_WIDTH_ID = accessor<Int>(Display.TextDisplay::class, "DATA_LINE_WIDTH_ID")
    val BACKGROUND_COLOR_ID = accessor<Int>(Display.TextDisplay::class, "DATA_BACKGROUND_COLOR_ID")
    val TEXT_OPACITY_ID = accessor<Byte>(Display.TextDisplay::class, "DATA_TEXT_OPACITY_ID")
    val STYLE_FLAGS_ID = accessor<Byte>(Display.TextDisplay::class, "DATA_STYLE_FLAGS_ID")
}
