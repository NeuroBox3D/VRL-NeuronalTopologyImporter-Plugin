/// package's name
package edu.gcsc.vrl.neti;

/// imports
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.system.VMessage;
import eu.mihosoft.vrl.system.VPluginConfigurator;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author stephanmg <stephan@syntaktischer-zucker.de>
 */
@ComponentInfo(name = "NeuronalTopologyImporterPathUtil", category = "/UG4/VRL-Plugins/Neuro/NeuronalTopologyImporter")
public class NeuronalTopologyImporterPathUtil implements Serializable {
	/// private final static members
	private static final long serialVersionUID = 1L;
	private static final String RESOURCE_FOLDER = "resources";
	private static final String PLUGIN_NAME = "NeuronalTopologyImporter";
	
	/// private static members
	private static File pathToPlugin = null;
	
	/**
	 * @brief below must be factored out into another method / class as utility
	 * 	  then we can use the utility as follows in a component that needs pathes!:
	 * 
	 * 		PathUtility utility = new PathUtility(YOUR_PLUGIN_NAME);
	 * 		String resource_folder_path = utility.provide_us_the_path();
	 * 
	 * 		utility = new PathUtility(); /// guess the plugin name!
	 * 
	 * 		This snippet could then be used in e. g. NeuroBoxPathComponent
	 * 		i. e., build a compoent with String filename as input,
	 * 	        then we prepend the resource_folder_path, therefore in a gui 
	 *              and in a console app we get the correct pathes! (minimal overhead,
	 *              just need to specify the plugin or not then we guess the plugin name!)
	 * 		
	 * 		
	 * 
	 * @param fName
	 * @return
	 * @throws IOException 
	 */
	@MethodInfo(name = "get resource file", valueName = "file")
	public File getResourceFile(
		@ParamInfo(name = "file name") 
		String fName
	) throws IOException {
		System.err.println("Package name: " + getClass().getPackage().getName());
		System.err.println("Class: " + getClass().getCanonicalName());
		System.err.println("Class: " + getClass().getDeclaredFields().length);
		/// in case we run as a console app
		if (pathToPlugin == null) {
					File f = new File("/Users/stephan/Temp/console_apps/test30/.application/property-folder/plugins");

		FilenameFilter jarFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jar");
			}
		};

		ArrayList<Class> clazzes = new ArrayList<Class>();
		File[] files = f.listFiles(jarFilter);
		for (File file : files) {
			if (!file.isDirectory()) {
				System.out.print("     file:");
				
				URL jar = null;
				try {
					jar = file.toURI().toURL();
				} catch (MalformedURLException ex) {
					Logger.getLogger(ClassFinder.class.getName()).log(Level.SEVERE, null, ex);
				}
				ZipInputStream zip = null;
				try {
					zip = new ZipInputStream(jar.openStream());
				} catch (IOException ex) {
					Logger.getLogger(ClassFinder.class.getName()).log(Level.SEVERE, null, ex);
				}
				ZipEntry ze = null;

				List<String> list = new ArrayList<String>();
				try {
					while ((ze = zip.getNextEntry()) != null) {
						String entryName = ze.getName();
						if (entryName.contains("neti") && entryName.endsWith(".class")) {
							list.add(entryName);
							System.err.println("entry: " + entryName.replace("/", "."));
							try {
								System.err.println("to load: "+ entryName.substring(0, entryName.length()-6));
								clazzes.add(Class.forName(entryName.substring(0, entryName.length()-6).replace("/", ".")));
							} catch (ClassNotFoundException ex) {
								Logger.getLogger(ClassFinder.class.getName()).log(Level.SEVERE, null, ex);
								System.err.println("error!");
							}
						}
					}
				} catch (IOException ex) {
					Logger.getLogger(ClassFinder.class.getName()).log(Level.SEVERE, null, ex);
				}

			}
		}


		for (Class<?> clazz : clazzes) {
			System.err.println("declared fields: " + clazz.getDeclaredFields());
		}
			
			String plugin_name = PLUGIN_NAME;
			ClassFinder c = new ClassFinder();
			//List<Class<?>> classes = Arrays.asList(c.findAllClasses(getClass().getPackage().getName()));
			for (Class<?> clazz : clazzes) {
				System.err.println("class: " + clazz);
				if (clazz.getSuperclass().equals(VPluginConfigurator.class)) {
					String complete_name = clazz.getName();
					String[] splitted = complete_name.split("\\.");
					plugin_name = splitted[splitted.length-1];
					plugin_name = plugin_name.replace("PluginConfigurator", "");
				}
			}
			
			pathToPlugin = new File("dummy");
			String[] s = pathToPlugin.getAbsolutePath().split(File.separator);
			File f1 = new File(new File(StringUtils.join(Arrays.copyOf(s, s.length - 1), 
				File.separator)).getAbsolutePath() + 
				File.separator + "property-folder" + File.separator + "plugins" +
				File.separator + "VRL-" + plugin_name + "-Plugin" +
				File.separator + RESOURCE_FOLDER + File.separator + fName);
			
			if (!f1.exists()) {
				System.err.println("File not found: " + f1.getAbsolutePath());
			} else {
				return f1;
			}
			
			f1 = new File(new File(StringUtils.join(Arrays.copyOf(s, s.length - 1), 
				File.separator)).getAbsolutePath() + 
				File.separator + "property-folder" + File.separator + "plugins" +
				File.separator + "VRL-" + plugin_name + 
				File.separator + RESOURCE_FOLDER + File.separator + fName);

			if (!f1.exists()) {
				System.err.println("File also not found: " + f1.getAbsolutePath()); 
				System.err.println("Is you plugin registered with a cryptic name?");
			} else {
				return f1;
			}
			
			f1 = new File(new File(StringUtils.join(Arrays.copyOf(s, s.length - 1), 
				File.separator)).getAbsolutePath() + 
				File.separator + "property-folder" + File.separator + "plugins" +
				File.separator + PLUGIN_NAME +
				File.separator + RESOURCE_FOLDER + File.separator + fName);
			
			if (!f1.exists()) {
				System.err.println("File also not found... plugin installed? " + f1.getAbsolutePath());
				return null;
			} else {
				return f1;
			}
			
		/// in case we run as a GUI app
		} else {
			File f2 = new File(pathToPlugin.getAbsolutePath() + 
				File.separator + RESOURCE_FOLDER + File.separator, fName);

			if (!f2.exists()) {
				VMessage.exception("File not found",
					"The file '" + pathToPlugin.getAbsolutePath() 
						+ File.separator + RESOURCE_FOLDER + File.separator 
						+ fName + " can not be found.");
				return null;
			} else {
				return f2;
			}
		}
	}

	/**
	 * @brief get's the path to the plugin
	 * @return
	 */
	public static File get_path_to_plugin() {
		return pathToPlugin;
	}
	
	/**
	 * @brief set's the path to the plugin
	 * @param f 
	 */
	public static void set_path_to_plugin(File f) {
		
		String[] s = f.getAbsolutePath().split(File.separator);
		pathToPlugin = new File(StringUtils.join(Arrays.copyOf(s, s.length - 1), File.separator));
	}
}