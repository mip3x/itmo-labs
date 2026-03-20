#include <linux/module.h>
#include <linux/version.h>
#include <linux/kernel.h>
#include <linux/types.h>
#include <linux/kdev_t.h>
#include <linux/fs.h>
#include <linux/device.h>
#include <linux/cdev.h>
#include <linux/slab.h>
#include <linux/ctype.h>

static dev_t first;
static struct cdev c_dev;
static struct class *cl;

#define MAX_ENTRIES 1024
static int history[MAX_ENTRIES];
static int count = 0;

static int my_open(struct inode *i, struct file *f)
{
	printk(KERN_INFO "Driver: open()\n");
	return 0;
}

static int my_close(struct inode *i, struct file *f)
{
	printk(KERN_INFO "Driver: close()\n");
	return 0;
}

static ssize_t my_read(struct file *f, char __user *buf, size_t len, loff_t *off)
{
	char *out;
	int i;
	int out_len;
	size_t to_copy;
	size_t buf_size;

	buf_size = MAX_ENTRIES * 12;
	out = kmalloc(buf_size, GFP_KERNEL);
	if (!out)
		return -ENOMEM;

	for (i = 0; i < count; i++)
		out_len += scnprintf(out + out_len, buf_size - out_len, "%d\n", history[i]);

	if (*off >= out_len) {
		kfree(out);
		return 0;
	}

	to_copy = min(len, (size_t)(out_len - *off));
	if (copy_to_user(buf, out + *off, to_copy)) {
		kfree(out);
		return -EFAULT;
	}

	*off += to_copy;
	kfree(out);

	return to_copy;
}

static ssize_t my_write(struct file *f, const char __user *buf, size_t len, loff_t *off)
{
	char *kbuf;
	int letter_count = 0;
	int i;

	kbuf = kmalloc(len + 1, GFP_KERNEL);
	if (!kbuf)
		return -ENOMEM;

	if (copy_from_user(kbuf, buf, len)) {
        kfree(kbuf);
		return -EFAULT;
	}
	kbuf[len] = '\0';

	for (i = 0; i < len; i++) {
        if (isalpha(kbuf[i])) {
            letter_count++;
        }
    }

	if (count >= MAX_ENTRIES) {
		kfree(kbuf);
		return -ENOSPC;
	}
	history[count++] = letter_count;

	printk(KERN_INFO "Driver: write() %zu bytes, %d letters\n", len, letter_count);
    kfree(kbuf);

	return len;
}

static struct file_operations mychdev_fops = {.owner = THIS_MODULE,
					      .open = my_open,
					      .release = my_close,
					      .read = my_read,
					      .write = my_write};

static int __init ch_drv_init(void)
{
	printk(KERN_INFO "Hello!\n");
	if (alloc_chrdev_region(&first, 0, 1, "ch_dev") < 0) {
		return -1;
	}

	if ((cl = class_create(THIS_MODULE, "chardrv")) == NULL) {
		unregister_chrdev_region(first, 1);
		return -1;
	}

	if (device_create(cl, NULL, first, NULL, "mychdev") == NULL) {
		class_destroy(cl);
		unregister_chrdev_region(first, 1);
		return -1;
	}

	cdev_init(&c_dev, &mychdev_fops);

	if (cdev_add(&c_dev, first, 1) == -1) {
		device_destroy(cl, first);
		class_destroy(cl);
		unregister_chrdev_region(first, 1);
		return -1;
	}
	return 0;
}

static void __exit ch_drv_exit(void)
{
	cdev_del(&c_dev);
	device_destroy(cl, first);
	class_destroy(cl);
	unregister_chrdev_region(first, 1);
	printk(KERN_INFO "Bye!!!\n");
}

module_init(ch_drv_init);
module_exit(ch_drv_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Author");
MODULE_DESCRIPTION("The first kernel module");
