import {TestBed} from '@angular/core/testing';

import {AuthenticationErrorInterceptor} from './authentication-error.interceptor';

describe('AuthenticationErrorInterceptor', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      AuthenticationErrorInterceptor
    ]
  }));

  it('should be created', () => {
    const interceptor: AuthenticationErrorInterceptor = TestBed.inject(AuthenticationErrorInterceptor);
    expect(interceptor).toBeTruthy();
  });
});
