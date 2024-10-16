package nl.xandermarc.mc

import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import java.util.Arrays.asList

@Suppress("UnstableApiUsage")
class XMCLoader : PluginLoader {
    override fun classloader(pluginClasspathBuilder: PluginClasspathBuilder) {
        val dependencies = asList(
            "org.jetbrains.kotlin:kotlin-stdlib:2.0.20",
            "org.jetbrains.kotlin:kotlin-reflect:2.0.20"
        )
        for (dependency in dependencies) {
            val resolver = MavenLibraryResolver()
            resolver.addDependency(
                Dependency(DefaultArtifact(dependency), null)
            )
            pluginClasspathBuilder.addLibrary(resolver)
        }
    }
}
