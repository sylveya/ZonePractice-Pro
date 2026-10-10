<img width="2000" height="1000" alt="image(6)" src="https://i.imgur.com/ydTpj5d.png" />

---

# Permissions

The full list is in [`plugin.yml`](../core/src/main/resources/plugin.yml). Below are only the nodes it does not declare.

## Death effects

| Permission                           | Usage             |
|--------------------------------------|-------------------|
| `ap.cosmetics.deatheffect.none`      | No death effect   |
| `ap.cosmetics.deatheffect.flame`     | Flame effect      |
| `ap.cosmetics.deatheffect.lightning` | Lightning effect  |
| `ap.cosmetics.deatheffect.firework`  | Firework effect   |
| `ap.cosmetics.deatheffect.explosion` | Explosion effect  |
| `ap.cosmetics.deatheffect.blood`     | Blood effect      |
| `ap.cosmetics.deatheffect.enchant`   | Enchant effect    |
| `ap.cosmetics.deatheffect.ender`     | Ender effect      |
| `ap.cosmetics.deatheffect.hearts`    | Hearts effect     |
| `ap.cosmetics.deatheffect.ice`       | Ice effect        |
| `ap.cosmetics.deatheffect.supernova` | Supernova effect  |
| `ap.cosmetics.deatheffect.voidstorm` | Voidstorm effect  |
| `ap.cosmetics.deatheffect.phoenix`   | Phoenix effect    |
| `ap.cosmetics.deatheffect.comet`     | Comet effect      |
| `ap.cosmetics.deatheffect.*`         | All death effects |

## Armor trims

| Permission                            | Usage                   |
|---------------------------------------|-------------------------|
| `ap.cosmetics.armortrim.pattern.<id>`  | Use an armor trim pattern  |
| `ap.cosmetics.armortrim.material.<id>` | Use an armor trim material |

`<id>` is the trim name from your Minecraft version, for example `sentry`, `vex`, `amethyst` or `netherite`.

## Shield layouts

| Permission                               | Usage                              |
|------------------------------------------|------------------------------------|
| `ap.cosmetics.shield.use`               | Open the shield cosmetics          |
| `ap.cosmetics.shield.layouts.<1-21>`    | Use up to that many shield layouts |
| `ap.cosmetics.shield.layouts.unlimited` | Use unlimited shield layouts       |

## Groups

| Permission        | Usage                                          |
|-------------------|------------------------------------------------|
| `ap.group.<name>` | Player group — see [`groups.yml`](../core/src/main/resources/groups.yml) |

## Staff mode

| Permission             | Usage                                       |
|------------------------|---------------------------------------------|
| `ap.staffmode.follow` | Follow other staff members (not registered) |