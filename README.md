# wicket-bits
Common Wicket Components

[![Build Status](https://travis-ci.org/Mustard/wicket-bits.svg?branch=master)](https://travis-ci.org/Mustard/wicket-bits)

## reCAPTCHA Form Component

A simple form component that adds and validates the google [reCAPTCHA](https://www.google.com/recaptcha/intro/index.html)

```java
Form form = new Form();
form.add(new CaptchaFormComponent("captcha");
```
