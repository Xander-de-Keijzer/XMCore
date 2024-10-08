package nl.xandermarc.mc;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class PaperPluginLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder pluginClasspathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addDependency(
                new Dependency(
                    new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib:2.0.20"),
                    null
                )
        );
        resolver.addDependency(
                new Dependency(
                    new DefaultArtifact("org.jetbrains.kotlin:kotlin-reflect:2.0.20"),
                    null
                )
        );
        pluginClasspathBuilder.addLibrary(resolver);
    }

}