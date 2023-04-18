package tw.nesquate.TranX.money;

import net.minecraft.entity.player.PlayerEntity;
import tw.nesquate.TranX.exception.command.NullUUIDException;
import tw.nesquate.TranX.exception.money.InsufficientBalance;
import tw.nesquate.TranX.exception.money.MinusMoneyException;
import tw.nesquate.TranX.exception.money.NullMoneyException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

public class Money {
    private final HashMap<String, BigDecimal> money = new HashMap<>();

    public BigDecimal getMoney(UUID uuid) throws NullMoneyException, NullUUIDException {
        if(uuid == null){
            throw new NullUUIDException();
        }

        BigDecimal money = this.money.get(uuid.toString());

        if (money == null) {
            throw new NullMoneyException();
        }

        return new BigDecimal(money.toPlainString());
    }

    public void newMoneyRecord(UUID uuid) throws NullUUIDException {
        if(!isExist(uuid)){
            this.recordMoney(uuid, new BigDecimal(0));
        }
    }

    private boolean isExist(UUID uuid){
        for(String uuidString : this.money.keySet()){
            if(uuidString.equals(uuid.toString())){
                return true;
            }
        }
        return false;
    }

    private void recordMoney(UUID uuid, BigDecimal money) throws NullUUIDException {
        if(uuid == null){
            throw new NullUUIDException();
        }

        this.money.put(uuid.toString(), money);
    }

    private void compareZero(BigDecimal a) throws MinusMoneyException {
        if (a.compareTo(new BigDecimal(0)) < 0) {
            throw new MinusMoneyException();
        }
    }

    public void transfer(PlayerEntity from, PlayerEntity to, BigDecimal money) throws MinusMoneyException, InsufficientBalance {
        try {
            compareZero(money);

            UUID fromUUID = from.getUuid();
            UUID toUUID = to.getUuid();

            BigDecimal fromMoney = this.money.get(fromUUID.toString());
            if(fromMoney.compareTo(money) < 0){
                throw new InsufficientBalance();
            }
            fromMoney = fromMoney.subtract(money);
            this.money.put(fromUUID.toString(), fromMoney);

            BigDecimal toMoney = this.money.get(toUUID.toString());
            toMoney = toMoney.add(money);
            this.money.put(toUUID.toString(), toMoney);

        } catch (MinusMoneyException e) {
            throw new MinusMoneyException();
        } catch (InsufficientBalance e) {
            throw new InsufficientBalance();
        }
    }

    public void deposit(PlayerEntity player, int count) throws NullUUIDException, NullMoneyException {
        try{
            UUID uuid = player.getUuid();
            BigDecimal money = this.getMoney(uuid);

            money = money.add(new BigDecimal(count));

            this.recordMoney(uuid, money);
        } catch (NullUUIDException e) {
            throw new NullUUIDException();
        } catch (NullMoneyException e) {
            throw new NullMoneyException();
        }
    }

    public void withdraw(PlayerEntity player, int count) throws NullUUIDException, NullMoneyException, InsufficientBalance {
        try{
            UUID uuid = player.getUuid();
            BigDecimal money = this.getMoney(uuid);
            if(money.compareTo(new BigDecimal(count)) < 0){
                throw new InsufficientBalance();
            }
            money = money.subtract(new BigDecimal(count));
            this.recordMoney(uuid, money);
        } catch (NullUUIDException e) {
            throw new NullUUIDException();
        } catch (NullMoneyException e) {
            throw new NullMoneyException();
        } catch (InsufficientBalance e) {
            throw new InsufficientBalance();
        }
    }
}
