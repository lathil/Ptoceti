package com.ptoceti.osgi.modbusdevice.impl.command;

import com.ptoceti.osgi.modbus.ModbusDriver;
import com.ptoceti.osgi.modbusdevice.ModbusCommand;
import com.ptoceti.osgi.modbusdevice.ModbusDevice;

import java.util.ArrayList;
import java.util.List;

public class ReadCoilsStatusCommand extends ModbusCommand {

    byte unitId;
    int coilId;
    int nbCoils;

    public ReadCoilsStatusCommand(ModbusDevice modbusDevice, byte unitId, int coilId, int nbCoils) {
        super(modbusDevice);
        this.unitId = unitId;
        this.coilId = coilId;
        this.nbCoils = nbCoils;
    }

    @Override
    public List<Boolean> call() throws Exception {
        ModbusDriver driver = getModbusDevice().getModbusDriver();
        List<Boolean> result = new ArrayList<>();
        if (driver != null) {
            byte[] coilsValues = driver.readCoilsStatus(unitId, coilId, nbCoils);
            if (coilsValues.length > 0) {
                int nbCoilsIndex = 0;
                for (int index = 0; (index < coilsValues.length) && (nbCoilsIndex < nbCoils); index++) {
                    byte nextByte = coilsValues[index];
                    for (int bitIndex = 0; (bitIndex < 8) && (nbCoilsIndex < nbCoils); bitIndex++, nbCoilsIndex++) {
                        if (((nextByte >>> bitIndex) & 1) == 1) {
                            result.add(Boolean.TRUE);
                        } else {
                            result.add(Boolean.FALSE);
                        }
                    }
                }
            }
        }
        return result;
    }

}
