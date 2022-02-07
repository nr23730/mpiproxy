package de.uksh.medic.mpiproxy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.uksh.medic.mpiproxy.settings.ConfigurationLoader;
import de.uksh.medic.mpiproxy.settings.Settings;

@SpringBootApplication
public class MpiProxy {

	public static void main(String[] args) throws FileNotFoundException {
        InputStream settingsYaml = ClassLoader.getSystemClassLoader().getResourceAsStream("settings.yaml");
        if (args.length == 1) {
            settingsYaml = new FileInputStream(args[0]);
        }

        ConfigurationLoader configLoader = new ConfigurationLoader();
        configLoader.loadConfiguration(settingsYaml, Settings.class);

		SpringApplication.run(MpiProxy.class, args);
	}

}
