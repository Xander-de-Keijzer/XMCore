package nl.xandermarc.mc.paper

import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import java.util.*

class PaperPluginLoader : PluginLoader {
    override fun classloader(pluginClasspathBuilder: PluginClasspathBuilder?) {
        // URL : https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-reflect/2.0.20/kotlin-reflect-2.0.20.jar
        // URK : https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core/1.9.0/
        val dependencies = Arrays.asList(
            Arrays.asList(
                "org.jetbrains.kotlin:kotlin-stdlib:2.0.20",
                "org/jetbrains/kotlin/kotlin-stdlib/2.0.20",
                "kotlin-stdlib-2.0.20"
            ),
            Arrays.asList(
                "org.jetbrains.kotlin:kotlin-reflect:2.0.20",
                "org/jetbrains/kotlin/kotlin-reflect/2.0.20",
                "kotlin-reflect-2.0.20"
            ),
            Arrays.asList(
                "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0",
                "org/jetbrains/kotlinx/kotlinx-coroutines-core/1.9.0",
                "kotlinx-coroutines-core-1.9.0"
            ),
            Arrays.asList(
                "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.9.0",
                "org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.9.0",
                "kotlinx-coroutines-core-jvm-1.9.0"
            )
        )
        try {
            for (dependency in dependencies) {
                val resolver = MavenLibraryResolver()
                resolver.addDependency(
                    Dependency(DefaultArtifact(dependency[0]), null)
                )
                pluginClasspathBuilder?.addLibrary(resolver)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println(e.message)
        }
    }
}
