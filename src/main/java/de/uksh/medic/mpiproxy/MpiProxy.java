package de.uksh.medic.mpiproxy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import de.uksh.medic.mpiproxy.settings.ConfigurationLoader;
import de.uksh.medic.mpiproxy.settings.Settings;

@SpringBootApplication
public class MpiProxy {

	public static void main(String[] args) throws IOException {
		Resource resource = new ClassPathResource("settings.yaml");
        InputStream settingsYaml = resource.getInputStream();
        if (args.length == 1) {
			settingsYaml.close();
            settingsYaml = new FileInputStream(args[0]);
        }

        ConfigurationLoader configLoader = new ConfigurationLoader();
        configLoader.loadConfiguration(settingsYaml, Settings.class);
		settingsYaml.close();
		SpringApplication.run(MpiProxy.class, args);
	}

}
