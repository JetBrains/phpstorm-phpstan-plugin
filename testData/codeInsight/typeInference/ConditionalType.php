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