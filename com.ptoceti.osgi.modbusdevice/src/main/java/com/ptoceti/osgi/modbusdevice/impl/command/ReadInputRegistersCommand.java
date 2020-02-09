package com.ptoceti.osgi.modbusdevice.impl.command;

import com.ptoceti.osgi.modbus.ModbusDriver;
import com.ptoceti.osgi.modbusdevice.ModbusCommand;
import com.ptoceti.osgi.modbusdevice.ModbusDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReadInputRegistersCommand extends ModbusCommand<List<Integer>> {

    byte unitId;
    int registerId;
    int nbRegisters;

    public ReadInputRegistersCommand(ModbusDevice modbusDevice, byte unitId, int registerId, int nbRegisters) {
        super(modbusDevice);
        this.unitId = unitId;
        this.registerId = registerId;
        this.nbRegisters = nbRegisters;
    }

    @Override
    public List<Integer> call() throws Exception {
        ModbusDriver driver = getModbusDevice().getModbusDriver();
        List<Integer> result = new ArrayList<>();
        if (driver != null) {
            int[] registersValues = driver.readHoldingRegisters(unitId, registerId, nbRegisters);
            if (registersValues.length > 0) {
                result = Arrays.stream(registersValues).boxed().collect(Collectors.toList());
            }
        }
        return result;
    }
}
