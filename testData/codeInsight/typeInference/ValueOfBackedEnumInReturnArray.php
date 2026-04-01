<?php

enum UserRoleEnum: string
{
    case Admin = 'admin';
    case User = 'user';
}

class User
{
    /**
     * @phpstan-return array<value-of<UserRoleEnum>>
     */
    public function getRoles(): array
    {
        return [UserRoleEnum::Admin->value, UserRoleEnum::User->value];
    }
}

<type value="string[]">(new User())->getRoles()</type>;
