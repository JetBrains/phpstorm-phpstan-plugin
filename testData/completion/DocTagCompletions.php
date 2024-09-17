<?php

namespace phpstanCompletions;

interface Bar {
}

/**
 * @php<caret>
 */
trait Foo {
}


class Lorem implements Bar {
  use Foo;
}