import {Injectable} from '@angular/core';

@Injectable()

export abstract class JwtStorage {

  public TOKEN_NAME = 'token';

  public abstract get(): string;

  public abstract put(token: string): void;

  public abstract clear(): void;
}

@Injectable()
export class JwtLocalStorage extends JwtStorage {
  get(): string {
    return localStorage.getItem(this.TOKEN_NAME);
  }

  put(token: string): void {
    localStorage.setItem(this.TOKEN_NAME, token);
  }

  clear(): void {
    localStorage.removeItem(this.TOKEN_NAME);
  }
}

@Injectable()
export class JwtSessionStorage extends JwtStorage {
  get(): string {
    return sessionStorage.getItem(this.TOKEN_NAME);
  }

  put(token: string): void {
    sessionStorage.setItem(this.TOKEN_NAME, token);
  }

  clear(): void {
    sessionStorage.removeItem(this.TOKEN_NAME);
  }
}
