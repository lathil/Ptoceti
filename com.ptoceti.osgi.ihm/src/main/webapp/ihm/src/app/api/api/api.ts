export * from './lobby.service';
import {LobbyService} from './lobby.service';
export * from './mqtt.service';
import {MqttService} from './mqtt.service';
export * from './series.service';
import {SeriesService} from './series.service';
export * from './wires.service';
import {WiresService} from './wires.service';
export const APIS = [LobbyService, MqttService, SeriesService, WiresService];
