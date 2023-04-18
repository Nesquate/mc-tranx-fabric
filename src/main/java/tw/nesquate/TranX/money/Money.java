package tw.nesquate.TranX.money;

import net.minecraft.entity.player.PlayerEntity;
import tw.nesquate.TranX.database.AbstractDatabase;
import tw.nesquate.TranX.exception.command.NullUUIDException;
import tw.nesquate.TranX.exception.general.DatabaseErrorException;
import tw.nesquate.TranX.exception.money.InsufficientBalance;
import tw.nesquate.TranX.exception.money.MinusMoneyException;
import tw.nesquate.TranX.exception.money.NullMoneyException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

public class Money {
    private final AbstractDatabase db;

    public Money(AbstractDatabase database){
        db = database;
    }

    public BigDecimal getMoney(UUID uuid) throws NullUUIDException, DatabaseErrorException {
        if(uuid == null){
            throw new NullUUIDException();
        }

        BigDecimal money = db.read(uuid.toString());

        if (money == null) {
            throw new DatabaseErrorException();
        }

        return new BigDecimal(money.toPlainString());
    }

    public void newMoneyRecord(UUID uuid) throws NullUUIDException, DatabaseErrorException {
        if(!isExist(uuid)){
            this.recordMoney(uuid, new BigDecimal(0));
        }
    }

    private boolean isExist(UUID uuid){
        HashMap<String, BigDecimal> temp = new HashMap<>();
        db.readAll(temp);

        for(String uuidString : temp.keySet()){
            if(uuidString.equals(uuid.toString())){
                return true;
            }
        }
        return false;
    }

    private void recordMoney(UUID uuid, BigDecimal money) throws NullUUIDException, DatabaseErrorException {
        if(uuid == null){
            throw new NullUUIDException();
        }
        HashMap<String, BigDecimal> temp = new HashMap<>();
        temp.put(uuid.toString(), money);

        if(!db.write(temp)){
            throw new DatabaseErrorException();
        }
    }

    private void compareZero(BigDecimal a) throws MinusMoneyException {
        if (a.compareTo(new BigDecimal(0)) < 0) {
            throw new MinusMoneyException();
        }
    }

    public void transfer(PlayerEntity from, PlayerEntity to, BigDecimal money) throws MinusMoneyException, InsufficientBalance, NullUUIDException, DatabaseErrorException {
        try {
            compareZero(money);

            UUID fromUUID = from.getUuid();
            UUID toUUID = to.getUuid();

            BigDecimal fromMoney = getMoney(fromUUID);
            if(fromMoney.compareTo(money) < 0){
                throw new InsufficientBalance();
            }
            fromMoney = fromMoney.subtract(money);
            recordMoney(fromUUID, fromMoney);

            BigDecimal toMoney = getMoney(toUUID);
            toMoney = toMoney.add(money);
            recordMoney(toUUID, toMoney);

        } catch (MinusMoneyException e) {
            throw new MinusMoneyException();
        } catch (InsufficientBalance e) {
            throw new InsufficientBalance();
        } catch (NullUUIDException e) {
            throw new NullUUIDException();
        } catch (DatabaseErrorException e) {
            throw new DatabaseErrorException();
        }
    }

    public void deposit(PlayerEntity player, int count) throws NullUUIDException, NullMoneyException, DatabaseErrorException {
        try{
            UUID uuid = player.getUuid();
            BigDecimal money = getMoney(uuid);

            money = money.add(new BigDecimal(count));

            this.recordMoney(uuid, money);
        } catch (NullUUIDException e) {
            throw new NullUUIDException();
        } catch (DatabaseErrorException e) {
            throw new DatabaseErrorException();
        }
    }

    public void withdraw(PlayerEntity player, int count) throws NullUUIDException, NullMoneyException, InsufficientBalance, DatabaseErrorException {
        try{
            UUID uuid = player.getUuid();
            BigDecimal money = getMoney(uuid);
            if(money.compareTo(new BigDecimal(count)) < 0){
                throw new InsufficientBalance();
            }
            money = money.subtract(new BigDecimal(count));
            recordMoney(uuid, money);
        } catch (NullUUIDException e) {
            throw new NullUUIDException();
        } catch (InsufficientBalance e) {
            throw new InsufficientBalance();
        } catch (DatabaseErrorException e) {
            throw new DatabaseErrorException();
        }
    }
}
