// Register the active player model for the Enhanced temporal pose path.
extern unsigned int _ZNK6Player14GetBodyModelIDEjb(char *c, unsigned int a, int b);
extern void _ZN9Animation7AdvanceEv(char *anim);
extern int _ZN6Player6IsAnimEj(char *c, unsigned int a);

void Player_AdvanceAnims(char *self)
{
    unsigned int id =
        _ZNK6Player14GetBodyModelIDEjb(self, *(unsigned char *)(self + 0x6db), 1);
    char *model = (char *)*(int *)(self + (id << 2) + 0xdc);
    *(char **)0x02004DF8 = model;
    _ZN9Animation7AdvanceEv(model + 0x50);
    if (_ZN6Player6IsAnimEj(self, 0x9a) || _ZN6Player6IsAnimEj(self, 0x19) ||
        _ZN6Player6IsAnimEj(self, 0xac) || _ZN6Player6IsAnimEj(self, 0x6b) ||
        _ZN6Player6IsAnimEj(self, 0x85) || _ZN6Player6IsAnimEj(self, 0x94) ||
        _ZN6Player6IsAnimEj(self, 0x98) || _ZN6Player6IsAnimEj(self, 0x99)) {
        *(int *)(self + *(int *)(self + 8) * 0x14 + 0x1e4) = 0;
    }
    _ZN9Animation7AdvanceEv(self + 0x1dc + *(unsigned char *)(self + 0x6db) * 0x14);
}
