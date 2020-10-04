import {Component, OnInit} from '@angular/core';
import {ViewEncapsulation} from '@angular/core';
import {Router, RouterState, ActivatedRoute, ParamMap} from '@angular/router';

@Component({
  selector: 'app-root',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'ihm';

  router: Router;

  constructor(router: Router) {

    this.router = router;
  }

  ngOnInit(): void {
    this.router.navigate(['/dashboard']);
  }


}
