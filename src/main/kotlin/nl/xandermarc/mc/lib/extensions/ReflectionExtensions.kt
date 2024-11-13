@file:JvmName("Extensions")
@file:JvmMultifileClass
package nl.xandermarc.mc.lib.extensions

import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmName

fun KClass<*>.nameSuffix(suffix: String) = (simpleName ?: jvmName).lowercase().removeSuffix(suffix)
fun KClassifier?.subclassOf(kClass: KClass<*>) = (this as? KClass<*>)?.isSubclassOf(kClass) ?: false
