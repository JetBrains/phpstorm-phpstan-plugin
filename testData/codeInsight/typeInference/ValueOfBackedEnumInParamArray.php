<?php

enum UserRoleEnum: string
{
    case Admin = 'admin';
    case User = 'user';
}

class User
{
    /** @var string[] */
    private array $roles = [];

    /**
     * @phpstan-param array<value-of<UserRoleEnum>> $roles
     * @return User
     */
    public function setRoles(array $roles): static
    {
        <type value="string[]">$roles</type>;
        $this->roles = $roles;

        return $this;
    }
}
