# wicket-bits
Common Wicket Components

[![Build Status](https://travis-ci.org/Mustard/wicket-bits.svg?branch=master)](https://travis-ci.org/Mustard/wicket-bits)

## reCAPTCHA Form Component

A simple form component that adds and validates the google [reCAPTCHA](https://www.google.com/recaptcha/intro/index.html)

```html
<div wicket:id="captcha"></div>
```

```java
form.add(new CaptchaFormComponent("captcha");
```


## Stripe

### Credit Card Input

```html
<div wicket:id="stripe-cc"></div>
```

```java
form.add(new StripeCreditCardField("stripe-cc"));
```