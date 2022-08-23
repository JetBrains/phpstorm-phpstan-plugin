<?php
namespace AA;
/**
 * @phpstan-template T
 * @phpstan-template T1
 */
class A {
    /**
     * @phpstan-param T $t
     * @phpstan-param T1 $t2
     * @phpstan-return T&T1
     */
    function mirror($t, $t2) {
        return $t;
    }
}

$a = "a";
$b = 5;
<type value="int|string">$c</type> = (new A())->mirror($a, $b);

namespace BB;

/**
 * @phpstan-template T
 * @phpstan-template T1
 */
abstract class Base
{


    /**
     * @phpstan-var T
     */
    public $first;

    /**
     * @phpstan-var T1
     */
    public $second;
}

class P
{
}
class P1
{
}


/**
 * @phpstan-extends Base<P, P1>
 */
class Child extends Base
{
}

/**
 * @phpstan-extends Base<P>
 */
class ChildPartial extends Base
{
}

<type value="\BB\P">(new Child())->first</type>;
<type value="\BB\P1">(new Child())->second</type>;
<type value="\BB\P">(new ChildPartial())->first</type>;
<type value="">(new ChildPartial())->second</type>;