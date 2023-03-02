<?php

class A
{
    /**
     * @phpstan-assert string[] $arr
     * @psalm-check-type $bar = int
     * @psalm-type PhoneType = array{phone: string}
     * @phpstan-param-out int $a
     * @phpstan-if-this-is a<int>
     * @phpstan-assert-if-false int $this->test()
     * @phpstan-assert-if-true null $this->b
     * @template NewT
     * @phpstan-this-out self<NewT>
     * @phpstan-self-out self<NewT>
     */
    function <caret>a()
    {

    }
}
