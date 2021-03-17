<?php

class Some
{
    /** @phpstan-var int $a */
    private $a;

    /**
     * @phpstan-param string $param
     * @phpstan-return float
     */
    public function foo($param, $secondParam)
    {
        <type value="string">$param</type>;
        <type value="">$secondParam</type>;
        <type value="int">$this->a</type>;
    }
}

<type value="float">(new Some())->foo(1)</type>;
