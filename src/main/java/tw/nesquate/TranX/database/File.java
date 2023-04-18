package tw.nesquate.TranX.database;

import net.fabricmc.loader.api.FabricLoader;
import tw.nesquate.TranX.utils.Utils;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class File extends AbstractDatabase{

    public static final Path FABRIC_CONFIG_PATH = FabricLoader.getInstance().getConfigDir();
    public static final Path TRANX_FOLDER = Paths.get(FABRIC_CONFIG_PATH.toString() + "\\TranX");
    private final Path TRANX_PLAIN_TXT_FILE = Paths.get(TRANX_FOLDER + "\\tranx-money.txt");

    private final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;

    public File() throws IOException {
        Utils.LOGGER.info("Loading file database");
        Utils.LOGGER.debug("TranX path:"+ TRANX_FOLDER);
        Utils.LOGGER.debug("TranX file database path:"+ TRANX_PLAIN_TXT_FILE);
        File.createFolder();
        createFile();
    }

    public static void createFolder() throws IOException {
        if(Files.notExists(TRANX_FOLDER)){
            Files.createDirectory(TRANX_FOLDER);
        }
    }

    private void createFile() throws IOException {
        try{
            if(Files.notExists(TRANX_PLAIN_TXT_FILE)){
                Files.createFile(TRANX_PLAIN_TXT_FILE);
            }
        } catch (IOException e) {
            throw new IOException();
        }
    }

    @Override
    public BigDecimal read(String key) {
        try{
            BufferedReader reader = Files.newBufferedReader(TRANX_PLAIN_TXT_FILE, CHARSET_UTF_8);

            String line;

            while((line = reader.readLine()) != null){
                if(line.startsWith(key)){
                    String[] pair = line.split(":");
                    if(pair.length == 2){
                        String value = pair[1];
                        reader.close();
                        return new BigDecimal(value);
                    }else{
                        return new BigDecimal(0);
                    }
                }
            }
            reader.close();
            return new BigDecimal(0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean write(HashMap<String, BigDecimal> pair) {
        HashMap<String, BigDecimal> temp = new HashMap<>();

        if(!readAll(temp)){
            return false;
        }

        String key = (String) (pair.keySet().toArray())[0];
        BigDecimal value = pair.get(key);
        temp.put(key ,value);

        return writeAll(temp);
    }

    @Override
    public boolean readAll(HashMap<String, BigDecimal> mapInMemory){
        try{
            BufferedReader reader = Files.newBufferedReader(TRANX_PLAIN_TXT_FILE, CHARSET_UTF_8);

            String keyValue;

            while((keyValue = reader.readLine()) != null){
                String[] keyValueArray = keyValue.split(":");

                if(keyValueArray.length == 2){
                    if(!mapInMemory.containsKey(keyValueArray[0])){
                        mapInMemory.put(keyValueArray[0], new BigDecimal(keyValueArray[1]));
                    }
                }

            }

            reader.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean writeAll(HashMap<String, BigDecimal> mapInMemory) {
        try{
            BufferedWriter writer = Files.newBufferedWriter(TRANX_PLAIN_TXT_FILE, CHARSET_UTF_8);
            writer.write("");
            writer.flush();

            for(String key: mapInMemory.keySet()){
                String valueString = mapInMemory.get(key).toPlainString();
                String line = key + ":" + valueString;
                writer.append(line);
                writer.newLine();
            }

            writer.flush();
            writer.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
