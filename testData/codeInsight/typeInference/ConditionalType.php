<?php

/**
 * @return ($size is positive-int ? int|string : array)
 */
function fillArray(int $size) : int|array { }

<type value="string|int|array">fillArray(0)</type>;

class is{}
/**
 * @return is
 */
function f(int $size){}

<type value="is">f()</type>;

class ConditionalThis
{
    /**
     * @return (static is int ? int : string)
     */
    public function foo(): int|string
    {

    }
}

<type value="int|string">(new ConditionalThis())->foo()</type>