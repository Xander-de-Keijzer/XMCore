package nl.xandermarc.mc.lib.entities

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

data object EntityData {
    private fun KClass<*>.member(name: String): KCallable<*> =
        members.firstOrNull { it.name == name } ?: throw NoSuchFieldException("$jvmName.$name")

    @Suppress("UNCHECKED_CAST")
    private fun <T> entityDataId(kClass: KClass<*>, name: String) =
        kClass.member(name).apply { isAccessible = true }.call() as EntityDataAccessor<T>

    // Entity
    val SHARED_FLAGS_ID = entityDataId<Byte>(Entity::class, "DATA_SHARED_FLAGS_ID")
    val AIR_SUPPLY_ID = entityDataId<Short>(Entity::class, "DATA_AIR_SUPPLY_ID")
    val CUSTOM_NAME = entityDataId<Optional<Component>>(Entity::class, "DATA_CUSTOM_NAME")
    val CUSTOM_NAME_VISIBLE = entityDataId<Boolean>(Entity::class, "DATA_CUSTOM_NAME_VISIBLE")
    val SILENT = entityDataId<Boolean>(Entity::class, "DATA_SILENT")
    val NO_GRAVITY = entityDataId<Boolean>(Entity::class, "DATA_NO_GRAVITY")
    val POSE = entityDataId<Pose>(Entity::class, "DATA_POSE")
    val TICKS_FROZEN = entityDataId<Int>(Entity::class, "DATA_TICKS_FROZEN")

    // Display
    val TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID =
        entityDataId<Int>(Display::class, "DATA_TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID")
    val TRANSFORMATION_INTERPOLATION_DURATION_ID =
        entityDataId<Int>(Display::class, "DATA_TRANSFORMATION_INTERPOLATION_DURATION_ID")
    val POS_ROT_INTERPOLATION_DURATION_ID = entityDataId<Int>(Display::class, "DATA_POS_ROT_INTERPOLATION_DURATION_ID")
    val TRANSLATION_ID = entityDataId<Vector3f>(Display::class, "DATA_TRANSLATION_ID")
    val SCALE_ID = entityDataId<Vector3f>(Display::class, "DATA_SCALE_ID")
    val LEFT_ROTATION_ID = entityDataId<Quaternionf>(Display::class, "DATA_LEFT_ROTATION_ID")
    val RIGHT_ROTATION_ID = entityDataId<Quaternionf>(Display::class, "DATA_RIGHT_ROTATION_ID")
    val BILLBOARD_RENDER_CONSTRAINTS_ID = entityDataId<Byte>(Display::class, "DATA_BILLBOARD_RENDER_CONSTRAINTS_ID")
    val BRIGHTNESS_OVERRIDE_ID = entityDataId<Int>(Display::class, "DATA_BRIGHTNESS_OVERRIDE_ID")
    val VIEW_RANGE_ID = entityDataId<Float>(Display::class, "DATA_VIEW_RANGE_ID")
    val SHADOW_RADIUS_ID = entityDataId<Float>(Display::class, "DATA_SHADOW_RADIUS_ID")
    val SHADOW_STRENGTH_ID = entityDataId<Float>(Display::class, "DATA_SHADOW_STRENGTH_ID")
    val WIDTH_ID = entityDataId<Float>(Display::class, "DATA_WIDTH_ID")
    val HEIGHT_ID = entityDataId<Float>(Display::class, "DATA_HEIGHT_ID")
    val GLOW_COLOR_OVERRIDE_ID = entityDataId<Int>(Display::class, "DATA_GLOW_COLOR_OVERRIDE_ID")

    // Display.BlockDisplay
    val BLOCK_STATE_ID = entityDataId<BlockState>(Display.BlockDisplay::class, "DATA_BLOCK_STATE_ID")

    // Display.ItemDisplay
    val ITEM_STACK_ID = entityDataId<ItemStack>(Display.ItemDisplay::class, "DATA_ITEM_STACK_ID")
    val ITEM_DISPLAY_ID = entityDataId<Byte>(Display.ItemDisplay::class, "DATA_ITEM_DISPLAY_ID")

    // Display.TextDisplay
    val TEXT_ID = entityDataId<Component>(Display.TextDisplay::class, "DATA_TEXT_ID")
    val LINE_WIDTH_ID = entityDataId<Int>(Display.TextDisplay::class, "DATA_LINE_WIDTH_ID")
    val BACKGROUND_COLOR_ID = entityDataId<Int>(Display.TextDisplay::class, "DATA_BACKGROUND_COLOR_ID")
    val TEXT_OPACITY_ID = entityDataId<Byte>(Display.TextDisplay::class, "DATA_TEXT_OPACITY_ID")
    val STYLE_FLAGS_ID = entityDataId<Byte>(Display.TextDisplay::class, "DATA_STYLE_FLAGS_ID")
}
