<?php

class Foo {}
class Bar {}
/**
 * @phpstan-type FooAlias = Foo
 * @phpstan-type BarAlias = Bar
 */
class Phone {

}

/**
 * @phpstan-import-type FooAlias from Phone as MyFooAlias
 * @phpstan-import-type BarAlias from Phone
 */
class User {
  /**
   * @phpstan-return MyFooAlias
   */
  public function f(){}

  /**
   * @phpstan-return BarAlias
   */
  public function f1(){}
}


<type value="Foo">(new User())->f()</type>;
<type value="Bar">(new User())->f1()</type>;