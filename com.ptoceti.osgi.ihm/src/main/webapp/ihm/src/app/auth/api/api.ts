export * from './login.service';
import {LoginService} from './login.service';

export * from './prefs.service';
import {PrefsService} from './prefs.service';

export const APIS = [LoginService, PrefsService];
