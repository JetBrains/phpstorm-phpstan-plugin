<?php

class Some
{
    /** @phpstan-var int $a */
    private $a;

    /**
     * @phpstan-param string $param
     * @phpstan-return float
     */
    public function foo($param)
    {
        <type value="string">$param</type>;
        <type value="int">$this->a</type>;
    }
}

<type value="float|void">(new Some())->foo(1)</type>;
