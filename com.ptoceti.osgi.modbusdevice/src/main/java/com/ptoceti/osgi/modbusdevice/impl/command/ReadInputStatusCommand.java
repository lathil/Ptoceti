package com.ptoceti.osgi.modbusdevice.impl.command;

import com.ptoceti.osgi.modbus.ModbusDriver;
import com.ptoceti.osgi.modbusdevice.ModbusCommand;
import com.ptoceti.osgi.modbusdevice.ModbusDevice;

import java.util.ArrayList;
import java.util.List;

public class ReadInputStatusCommand extends ModbusCommand<List<Boolean>> {
    byte unitId;
    int inputId;
    int nbInputs;

    public ReadInputStatusCommand(ModbusDevice modbusDevice, byte unitId, int inputId, int nbInputs) {
        super(modbusDevice);
        this.unitId = unitId;
        this.inputId = inputId;
        this.nbInputs = nbInputs;
    }

    @Override
    public List<Boolean> call() throws Exception {
        ModbusDriver driver = getModbusDevice().getModbusDriver();
        List<Boolean> result = new ArrayList<>();
        if (driver != null) {
            byte[] inputsValues = driver.readInputStatus(unitId, inputId, nbInputs);
            if (inputsValues.length > 0) {
                int nbInputsIndex = 0;
                for (int index = 0; (index < inputsValues.length) && (nbInputsIndex < nbInputs); index++) {
                    byte nextByte = inputsValues[index];
                    for (int bitIndex = 0; (bitIndex < 8) && (nbInputsIndex < nbInputs); bitIndex++, nbInputsIndex++) {
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
