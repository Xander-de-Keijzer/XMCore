package nl.xandermarc.mc.lib.extensions

import java.nio.file.Path

operator fun Path.invoke(path: String): Path = resolve(path).toAbsolutePath()
operator fun Path.invoke(): Path = this.toAbsolutePath()
operator fun Path.get(path: String): Path = resolve(path).toAbsolutePath()
