package tw.nesquate.TranX.database;

import java.math.BigDecimal;
import java.util.HashMap;

public abstract class AbstractDatabase {


    public BigDecimal read(String key){
        return new BigDecimal(0);
    }
    public boolean write(HashMap<String, BigDecimal> pair){
        return false;
    }

    public boolean readAll(HashMap<String, BigDecimal> mapInMemory){
        return false;
    }
    public boolean writeAll(HashMap<String, BigDecimal> mapInMemory) {
        return false;
    }
}
