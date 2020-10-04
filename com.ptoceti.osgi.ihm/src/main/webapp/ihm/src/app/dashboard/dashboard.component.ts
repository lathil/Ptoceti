import {Component, OnInit} from '@angular/core';
import {WiresService} from '../api/api/wires.service';
import {MqttService} from '../api/api/mqtt.service';
import {SeriesService} from '../api/api/series.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {

  wiresService: WiresService;
  mqttService: MqttService;
  seriesService: SeriesService;

  constructor(wiresService: WiresService, mqttService: MqttService, seriesService: SeriesService) {
    this.wiresService = wiresService;
    this.mqttService = mqttService;
    this.seriesService = seriesService;
  }

  ngOnInit(): void {
  }

}
