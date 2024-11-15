<?php

namespace Demo;

interface Container
{
    /**
     * @template T
     *
     * @param string|class-string<T> $id
     *
     * @return ($id is class-string<T> ? T : mixed)
     */
    public function get(string $id);
}

/** @var Container $container */
$test = <type value="\Demo\Container|mixed">$container->get(Container::class)</type>;
$test_2 = <type value="foo|mixed">$container->get('foo')</type>;
