<?php

/**
 * @phpstan-return Foo
 * @phpstan-param $a int cshdcygdc
 * @phpstan-param $b array<int, Foo>
 * @phpstan-throws Exception
 * @phpstan-template T of Foo with description
 * @phpstan-template-covariant M of Foo with description
 */
function fo<caret>o(int $a, $b) {

}

class Foo {

}